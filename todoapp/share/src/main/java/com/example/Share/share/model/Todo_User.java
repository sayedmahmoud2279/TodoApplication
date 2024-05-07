package com.example.Share.share.model;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="todo_user")
public class Todo_User {

    @Id
    private Integer id;

    private Integer todoId;

    private Integer userId;

    public Todo_User(Integer todoId, Integer userId){
        this.todoId = todoId;
        this.userId = userId;
    }
}
