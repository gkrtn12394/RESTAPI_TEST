package com.RestAPI.test.repository;

import com.RestAPI.test.dto.ContentDto;
import com.RestAPI.test.dto.MessageDto;
import com.RestAPI.test.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class MessageRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MessageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertContent(ContentDto content) {
        try {
            int strShtPath = this.jdbcTemplate.update("insert into contents (type, url, text) values (?, ?, ?)"
                    , content.getType(), content.getUrl(), content.getText());

            if (strShtPath < 0) return -1;

            return selectContent(content.getType(), content.getUrl(), content.getText()).getId();
        } catch (RuntimeException e) {
            throw new RuntimeException("insert error");
        }
    }

    public int insert(MessageDto message) {
        try {
            int contentId = insertContent(message.getContent());
            if(contentId < 0) return -1;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(message.getTimestamp());

            int result = this.jdbcTemplate.update("insert into messages (sender, recipient, contentId, time_stamp) values (?, ?, ?, ?)"
                                                , message.getSender(), message.getRecipient(), contentId, date);
            if(result < 0) return -1;

            List<MessageDto> messages = select(message.getRecipient(), 1, 1);
            if(messages.size() == 0) return -1;

            return messages.get(0).getId();
        } catch (RuntimeException e){
            return -1;
        }
    }

    public ContentDto selectContent(String type, String url, String text) {
        try {
            return this.jdbcTemplate.queryForObject("select id, type, url, text from contents where type = ? and url = ? and text = ?",
                    new String[]{type, url, text}, new RowMapper<ContentDto>() {
                @Override
                public ContentDto mapRow(ResultSet rs, int i) throws SQLException {
                    ContentDto dto = new ContentDto();

                    //가져온 사용자 정보 매핑
                    String id = rs.getString("id");
                    dto.setId(Integer.parseInt(id));
                    dto.setType(rs.getString("type"));
                    dto.setUrl(rs.getString("url"));
                    dto.setText(rs.getString("text"));

                    return dto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public ContentDto selectContent(String id) {
        try {
            return this.jdbcTemplate.queryForObject("select id, type, url, text from contents where id = ?",
                    new String[]{id}, new RowMapper<ContentDto>() {
                        @Override
                        public ContentDto mapRow(ResultSet rs, int i) throws SQLException {
                            ContentDto dto = new ContentDto();

                            //가져온 사용자 정보 매핑
                            dto.setId(Integer.parseInt(rs.getString("id")));
                            dto.setType(rs.getString("type"));
                            dto.setUrl(rs.getString("url"));
                            dto.setText(rs.getString("text"));

                            return dto;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<MessageDto> select(int recipient, int start, int limit) {
        return this.jdbcTemplate.query("select id, sender, recipient, contentId, time_stamp from messages where recipient = ? and id >= ? limit ?", new Integer[]{recipient, start, limit},
                new RowMapper<MessageDto>() {
                    @Override
                    public MessageDto mapRow(ResultSet rs, int i) throws SQLException {
                        MessageDto dto = new MessageDto();

                        try {
                            // 가지고온 데이터 매핑
                            dto.setId(Integer.parseInt(rs.getString("id")));
                            dto.setSender(Integer.parseInt(rs.getString("sender")));
                            dto.setRecipient(Integer.parseInt(rs.getString("recipient")));
                            dto.setContent(selectContent(rs.getString("contentId")));

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dto.setTimestamp(sdf.parse(rs.getString("time_stamp")));

                            return dto;
                        } catch (Exception e) {
                            return null;
                        }
                    }
                });
    }
}
