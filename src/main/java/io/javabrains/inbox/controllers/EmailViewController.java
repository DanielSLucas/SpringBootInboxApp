package io.javabrains.inbox.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.javabrains.inbox.email.Email;
import io.javabrains.inbox.email.EmailRepository;
import io.javabrains.inbox.emaillist.EmailListItem;
import io.javabrains.inbox.emaillist.EmailListItemKey;
import io.javabrains.inbox.emaillist.EmailListItemRepository;
import io.javabrains.inbox.folders.Folder;
import io.javabrains.inbox.folders.FolderRepository;
import io.javabrains.inbox.folders.FolderService;
import io.javabrains.inbox.folders.UnreadEmailStatsRepository;

@Controller
public class EmailViewController {
  @Autowired
	private FolderRepository folderRepository;

  @Autowired
	private FolderService folderService;

  @Autowired
	private EmailRepository emailRepository;

  @Autowired
  private EmailListItemRepository emailListItemRepository;

  @Autowired
  private UnreadEmailStatsRepository unreadEmailStatsRepository;

  @GetMapping(value = "/emails/{id}")
  public String emailView(
    @RequestParam String folder,
    @PathVariable UUID id,
    @AuthenticationPrincipal OAuth2User principal,
    Model model
  ) {
    if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
      return "index";
    } 
    
    String userId = principal.getAttribute("login");
    
    // Fetch folders
    List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
    List<Folder> userFolders = folderRepository.findAllById(userId);
    
    model.addAttribute("defaultFolders", defaultFolders);
    model.addAttribute("userFolders", userFolders);

    Optional<Email> optionalEmail = emailRepository.findById(id);

    if (!optionalEmail.isPresent()) {
      return "inbox";
    }

    Email email = optionalEmail.get();
    String toIds = String.join(", ", email.getTo());

    model.addAttribute("email", email);
    model.addAttribute("toIds", toIds);

    EmailListItemKey key = new EmailListItemKey(
      userId,
      folder,
      email.getId()
    );

    Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(key);

    if (optionalEmailListItem.isPresent()) {
      EmailListItem emailListItem = optionalEmailListItem.get();

      if (emailListItem.isUnread()) {
        emailListItem.setUnread(false);
        emailListItemRepository.save(emailListItem);
        unreadEmailStatsRepository.decrementUnreadCount(userId, folder);
      }
    }

    // Folder counters
    model.addAttribute("stats", folderService.mapCountToLabel(userId));

    return "email";
  }
}
