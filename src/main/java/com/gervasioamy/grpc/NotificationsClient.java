package com.gervasioamy.grpc;

import com.gervasioamy.proto.NotificationServiceGrpc;
import com.gervasioamy.proto.Notifications;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationsClient {

    private static final Logger logger = Logger.getLogger(NotificationsClient.class.getName());

    private final NotificationServiceGrpc.NotificationServiceBlockingStub blockingStub;
    private final ManagedChannel channel;

    public NotificationsClient()  {
        String target = "localhost:5000";
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
                .usePlaintext()
                .build();
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = NotificationServiceGrpc.newBlockingStub(channel);
        logger.info("Connected to the server...");
    }

    /** Send a notification server.
     * @return*/
    public int sendNotification(String message) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        logger.info("Will try to send a notification:  " + message + " ...");
        Notifications.Notification request = Notifications.Notification.newBuilder().
                setBody(message).
                setTimestamp(timestamp).
                build();

        Notifications.SendNotificationResponse response;
        try {
            response = blockingStub.sendNotification(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return -1;
        }
        logger.log(Level.INFO, "ID received: {0}", response.getId());
        return response.getId();
    }

    public List<Notifications.Notification> getAllNotifications() {
        Notifications.GetNotificationsRequest request = Notifications.GetNotificationsRequest.newBuilder().build();
        logger.info("Will try to get all notifications ...");
        try {
            Notifications.GetNotificationsResponse response = blockingStub.getNotifications(request);
            return response.getNotificationsList();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return null;
        }
    }

    public boolean removeNotification(int id) {
        Notifications.RemoveNotificationRequest request = Notifications.RemoveNotificationRequest.newBuilder().
                setId(id).
                build();
        logger.info("Will try to remove notification id:  " + id + " ...");
        try {
            Notifications.RemoveNotificationResponse response = blockingStub.removeNotification(request);
            return response.getRemoved();
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
    }

    public void shutdown() throws InterruptedException {
        // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
        // resources the channel should be shut down when it will no longer be used. If it may be used
        // again leave it running.
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

}
