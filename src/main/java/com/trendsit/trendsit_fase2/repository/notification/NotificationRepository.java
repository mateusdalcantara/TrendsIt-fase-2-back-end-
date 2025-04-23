package com.trendsit.trendsit_fase2.repository.notification;

import com.trendsit.trendsit_fase2.model.notification.Notification;
import com.trendsit.trendsit_fase2.model.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(Profile recipient);
    List<Notification> findByRecipientAndTypeOrderByCreatedAtDesc(Profile recipient, String type);

    List<Notification> findByRecipientAndTypeIn(Profile recipient, List<String> types);
    List<Notification> findByRecipientAndType(Profile currentUser, String vacancyRejected);
}