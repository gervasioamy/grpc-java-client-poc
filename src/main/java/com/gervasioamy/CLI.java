package com.gervasioamy;

import com.gervasioamy.grpc.NotificationsClient;
import com.gervasioamy.proto.Notifications;

import java.util.List;
import java.util.Scanner;

/**
 * Command Line Interface for sending notifications to the gRPC  server
 */
public class CLI {

    public static void run(NotificationsClient grpcClient){
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            //System.out.print("  ->");

            String[] input = scanner.nextLine().split(" ", 2);
            switch (input[0]) {
                case "list" :
                    printOut(grpcClient.getAllNotifications());
                    break;
                case "remove" :
                    try {
                        grpcClient.removeNotification(Integer.valueOf(input[1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Wrong ID, it must be a number. Try again");
                    }
                    break;
                case "send":
                    if (input.length < 2) {
                        System.out.println("Notification must not be empty. Try again");
                    } else {
                        grpcClient.sendNotification(input[1]);
                    }
                    break;
                case "exit":
                    exit = true;
                    break;

                default :
                    break;
            }
        }
    }
    /**
     * prints the lists out in the console
     */
    private static void printOut(List<Notifications.Notification> allNotifications) {
        for (int i = 0; i < allNotifications.size(); i++) {
            System.out.print("  [" + i + "] " );
            System.out.print("("+ allNotifications.get(i).getTimestamp() + ") - ");
            System.out.println(allNotifications.get(i).getBody());
        }
    }
}
