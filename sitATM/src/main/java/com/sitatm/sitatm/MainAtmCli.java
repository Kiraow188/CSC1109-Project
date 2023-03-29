package com.sitatm.sitatm;

import java.util.Scanner;

    
public class MainAtmCli {
    public static void main(String[] args) throws Exception {
        System.out.println("\n" +
                "           ____    __    ____  _______  __        ______   ______   .___  ___.  _______ \n" +
                "           \\   \\  /  \\  /   / |   ____||  |      /      | /  __  \\  |   \\/   | |   ____|\n" +
                "            \\   \\/    \\/   /  |  |__   |  |     |  ,----'|  |  |  | |  \\  /  | |  |__   \n" +
                "             \\            /   |   __|  |  |     |  |     |  |  |  | |  |\\/|  | |   __|  \n" +
                "              \\    /\\    /    |  |____ |  `----.|  `----.|  `--'  | |  |  |  | |  |____ \n" +
                "               \\__/  \\__/     |_______||_______| \\______| \\______/  |__|  |__| |_______|\n" +
                "                                                                                        \n" +
                "    .___________.  ______           _______. __  .___________.    ___   .___________..___  ___. \n" +
                "    |           | /  __  \\         /       ||  | |           |   /   \\  |           ||   \\/   | \n"
                +
                "    `---|  |----`|  |  |  |       |   (----`|  | `---|  |----`  /  ^  \\ `---|  |----`|  \\  /  | \n" +
                "        |  |     |  |  |  |        \\   \\    |  |     |  |      /  /_\\  \\    |  |     |  |\\/|  | \n"
                +
                "        |  |     |  `--'  |    .----)   |   |  |     |  |     /  _____  \\   |  |     |  |  |  | \n" +
                "        |__|      \\______/     |_______/    |__|     |__|    /__/     \\__\\  |__|     |__|  |__| ");
        System.out.println("\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("""
                Please select the following:
                [1] Teller Mode
                [2] Customer Mode
                """);
        int mode = sc.nextInt();
        if (mode == 1) {
            Teller.TellerMode();
        } else if (mode == 2) {
            Customer.CustomerMode();
        }
    }
}