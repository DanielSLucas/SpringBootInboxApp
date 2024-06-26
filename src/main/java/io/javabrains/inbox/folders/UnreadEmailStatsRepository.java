package io.javabrains.inbox.folders;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UnreadEmailStatsRepository extends CassandraRepository<UnreadEmailStats, String> {
  List<UnreadEmailStats> findAllById(String id);

  @Query("update unread_email_stats set unreadcount = unreadcount + 1 where user_id = ?0 and label = ?1")
  void incrementUnreadCount(String userId, String label);
  
  @Query("update unread_email_stats set unreadcount = unreadcount - 1 where user_id = ?0 and label = ?1")
  void decrementUnreadCount(String userId, String label);
}
