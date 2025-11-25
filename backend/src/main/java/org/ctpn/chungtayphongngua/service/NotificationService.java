package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.entity.Notification;
import org.ctpn.chungtayphongngua.entity.User;
import org.ctpn.chungtayphongngua.repository.NotificationRepository;
import org.ctpn.chungtayphongngua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Notification> getUnreadNotifications(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    public void createWelcomeNotification(User user) {
        String message = "Chào mừng " + user.getFullName() + " đến với Chung Tay Phòng Ngừa!";
        Notification notification = new Notification(user, message);
        notificationRepository.save(notification);
    }
}
