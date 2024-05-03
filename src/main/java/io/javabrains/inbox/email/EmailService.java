package io.javabrains.inbox.email;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.javabrains.inbox.emaillist.EmailListItem;
import io.javabrains.inbox.emaillist.EmailListItemKey;
import io.javabrains.inbox.emaillist.EmailListItemRepository;
import io.javabrains.inbox.folders.UnreadEmailStatsRepository;

@Service
public class EmailService {
  @Autowired
  private EmailRepository emailRepository;

  @Autowired
  private EmailListItemRepository emailListItemRepository;

  @Autowired
  private UnreadEmailStatsRepository unreadEmailStatsRepository;

  public void sendEmail(String from, List<String> to, String subject, String body) {
    Email email = Email.builder()
      .to(to)
      .from(from)
      .subject(subject)
      .body(body)
      .id(Uuids.timeBased())
      .build();

    emailRepository.save(email);

    to.forEach(toId -> {
      EmailListItem item = createEmailListItem(to, subject, email, toId, "Inbox");
      emailListItemRepository.save(item);
      unreadEmailStatsRepository.incrementUnreadCount(toId, "Inbox");
    });

    EmailListItem sentItemsEntry = createEmailListItem(to, subject, email, from, "Sent Items");
    sentItemsEntry.setUnread(false);
    emailListItemRepository.save(sentItemsEntry);
  }

  private EmailListItem createEmailListItem(List<String> to, String subject, Email email, String itemOwner, String folder) {
    EmailListItemKey key = new EmailListItemKey(itemOwner, folder, email.getId());
    EmailListItem item = new EmailListItem(key, to, subject, true, null);
    return item;
  }

  public boolean doesHaveAccess(Email email, String userId) {
    return userId.equals(email.getFrom()) || email.getTo().contains(userId);
  }

  public String getReplySubject(String subject) {
    return "Re: " + subject;
  }

  public String getReplyBody(Email email) {
    return "\n\n\n---------------------------------------- \n" + 
    "From: " + email.getFrom() + "\n" +
    "To: " +  email.getTo() + "\n\n" +
    email.getBody();
  }
}
