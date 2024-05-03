package io.javabrains.inbox;

import java.nio.file.Path;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.javabrains.inbox.email.Email;
import io.javabrains.inbox.email.EmailRepository;
import io.javabrains.inbox.emaillist.EmailListItem;
import io.javabrains.inbox.emaillist.EmailListItemKey;
import io.javabrains.inbox.emaillist.EmailListItemRepository;
import io.javabrains.inbox.folders.Folder;
import io.javabrains.inbox.folders.FolderRepository;
import io.javabrains.inbox.folders.UnreadEmailStatsRepository;


@SpringBootApplication
@RestController
public class InboxApp {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	EmailListItemRepository emailListItemRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	UnreadEmailStatsRepository unreadEmailStatsRepository;

	public static void main(String[] args) {
		SpringApplication.run(InboxApp.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void init() {
		
		folderRepository.save(new Folder("DanielSLucas", "Inbox", "blue"));
		folderRepository.save(new Folder("DanielSLucas", "Sent", "green"));
		folderRepository.save(new Folder("DanielSLucas", "Important", "yellow"));

		unreadEmailStatsRepository.incrementUnreadCount("DanielSLucas", "Inbox");
		unreadEmailStatsRepository.incrementUnreadCount("DanielSLucas", "Inbox");
		unreadEmailStatsRepository.incrementUnreadCount("DanielSLucas", "Inbox");

		for (int i = 0; i < 10; i++) {
			EmailListItemKey key = new EmailListItemKey(
				"DanielSLucas", 
				"Inbox", 
				Uuids.timeBased()
			);

			EmailListItem item = new EmailListItem(
				key, 
				Arrays.asList("DanielSLucas", "abc", "def"),
				"Subject " + i,
				true,
				null
			);

			emailListItemRepository.save(item);

			Email email = new Email(
				key.getTimeUUID(), 
				"DanielSLucas",
				item.getTo(),
				item.getSubject(),
				"Body " + i
			);

			emailRepository.save(email);
		}
	}

}
