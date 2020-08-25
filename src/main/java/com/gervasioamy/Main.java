package com.gervasioamy;

import com.gervasioamy.grpc.NotificationsClient;

public class Main {

    public static void main(String[] args) throws Exception {
        NotificationsClient grpcClient = new NotificationsClient();
        try {
            CLI.run(grpcClient);
        } finally {
          grpcClient.shutdown();
        }
    }

}


