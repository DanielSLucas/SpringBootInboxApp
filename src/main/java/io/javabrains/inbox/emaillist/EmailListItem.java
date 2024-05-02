package io.javabrains.inbox.emaillist;

import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "messages_by_user_folder")
public class EmailListItem {
  @PrimaryKey
  private EmailListItemKey key;

  @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
  private List<String> to;
  
  @CassandraType(type = Name.TEXT)
  private String subject;

  @CassandraType(type = Name.BOOLEAN)
  private boolean isUnread;

  @Transient
  private String agoTimeString;
}
