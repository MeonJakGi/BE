package com.beshow.backend.notification.service;

import com.beshow.backend.domain.alarm.Alarm;
import com.beshow.backend.domain.alarm.AlarmRepository;
import com.beshow.backend.domain.alarm.AlarmType;
import com.beshow.backend.domain.product.Product;
import com.beshow.backend.domain.shelf.Shelf;
import com.beshow.backend.domain.shelf.Slot;
import com.beshow.backend.domain.stock.Stock;
import com.beshow.backend.notification.dto.NotificationListResponse;
import com.beshow.backend.notification.dto.NotificationResponse;
import com.beshow.backend.notification.dto.ReadNotificationResponse;
import com.beshow.backend.notification.dto.UnreadCountResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final AlarmRepository alarmRepository;

    public NotificationListResponse getNotifications() {
        List<NotificationResponse> notifications = alarmRepository.findAllWithStockProductAndLocationOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();

        long unreadCount = alarmRepository.countByReadFalse();

        return new NotificationListResponse(
                notifications,
                unreadCount
        );
    }

    public UnreadCountResponse getUnreadCount() {
        long unreadCount = alarmRepository.countByReadFalse();
        return new UnreadCountResponse(unreadCount);
    }

    @Transactional
    public ReadNotificationResponse markAsRead(Long alarmId) {
        if (alarmId == null || alarmId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 알림 ID입니다. alarmId=" + alarmId);
        }

        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다. alarmId=" + alarmId));

        alarm.markAsRead();

        return new ReadNotificationResponse(
                alarm.getAlarmId(),
                true
        );
    }

    private NotificationResponse toResponse(Alarm alarm) {
        Stock stock = alarm.getStock();

        Product product = stock.getProduct();
        Slot slot = stock.getSlot();

        String productName = product.getProductName();
        String locationLabel = createLocationLabel(slot);
        String message = createMessage(
                alarm.getAlarmType(),
                productName,
                locationLabel
        );

        return new NotificationResponse(
                alarm.getAlarmId(),
                stock.getStockId(),
                alarm.getAlarmType().name(),
                message,
                productName,
                locationLabel,
                alarm.isRead(),
                alarm.getCreatedAt()
        );
    }

    private String createMessage(
            AlarmType alarmType,
            String productName,
            String locationLabel
    ) {
        return switch (alarmType) {
            case SHELF_EMPTY -> locationLabel + "의 " + productName + " 상품이 모두 소진되었습니다.";
            case NEED_CHECK -> locationLabel + "의 " + productName + " 상품이 기준 위치와 다른 곳에 진열되어 있습니다.";
        };
    }

    private String createLocationLabel(Slot slot) {
        Shelf shelf = slot.getShelf();
        String shelfName = createShelfName(shelf);

        return shelfName + " " + slot.getSlotCode();
    }

    private String createShelfName(Shelf shelf) {
        if (shelf.getShelfName() != null && !shelf.getShelfName().isBlank()) {
            return shelf.getShelfName();
        }

        return "선반 " + shelf.getShelfId();
    }
}