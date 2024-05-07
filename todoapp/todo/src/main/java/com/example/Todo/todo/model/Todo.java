package com.example.Todo.todo.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="todo")
public class Todo implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private Integer ownerId;

    private Integer folderId;

    // Foriegn Keys
    @OneToMany(mappedBy = "todo")
    private List<Task> tasks;
    
    public Object clone() throws CloneNotSupportedException 
    { 
        return super.clone(); 
    } 

    @Override
    public String toString() {
    return "Todo{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
    }
}
