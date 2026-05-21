package com.heapsteep.tools;

import com.heapsteep.model.Contact;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContactsTool {

    private final JdbcTemplate jdbcTemplate;

    public ContactsTool(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Tool(description = "Find contacts in a given city")
    public List<Contact> findContactsByCity(String city) {
        
        String sql = "SELECT name, email FROM contacts WHERE LOWER(city) = LOWER(?)";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Contact(rs.getString("name"), rs.getString("email")),
                city
        );
    }

    @Tool(description = "Format a list of contacts into CSV with headers : Name, Email")
    public String formatAsCsv(List<Contact> contacts){
        StringBuilder result = new StringBuilder("Name,Email\n");
        for (Contact contact : contacts){
            result.append(contact.getName()).append(",").append(contact.getEmail()).append("\n");
        }
        return result.toString();
    }
}
