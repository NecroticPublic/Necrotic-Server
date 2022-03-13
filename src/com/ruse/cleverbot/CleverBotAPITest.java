package com.ruse.cleverbot;

/*  CleverBot API Wrapper - A simple Java API Wrapper for CleverBot!
 *
 *  Copyright (C) 2016 Michael Flaherty // michaelwflaherty.com // michaelwflaherty@me.com
 * 
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see http://www.gnu.org/licenses/.
 */
 
import java.io.IOException;
import java.util.Scanner;

public class CleverBotAPITest
{
    public static void main(String[] args)
    {
        Scanner keyboard;
        CleverBotQuery botQuery;
        String input;
        boolean done;

        keyboard = new Scanner(System.in);

        do
        {
            System.out.print("Enter your message: ");
            input = keyboard.nextLine();
            done = input.equals("done");

            if (!done)
            {
                botQuery = new CleverBotQuery("CCCqzbJppXaWIkFhOidM_viBaEQ", input);
                try
                {
                    botQuery.sendRequest();
                    System.out.println("BOT: " + botQuery.getResponse());
                }
                catch (IOException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        } while(!done);

    }
}
