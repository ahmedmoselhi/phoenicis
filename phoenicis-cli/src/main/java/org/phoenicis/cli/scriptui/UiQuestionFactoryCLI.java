/*
 * Copyright (C) 2015-2017 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.phoenicis.cli.scriptui;

import org.phoenicis.configuration.security.Safe;
import org.phoenicis.scripts.ui.UiQuestionFactory;

import java.util.Scanner;

@Safe
public class UiQuestionFactoryCLI implements UiQuestionFactory {

    public UiQuestionFactoryCLI() {
        super();
    }

    @Override
    public void create(String questionText, Runnable yesCallback, Runnable noCallback) {
        String answer = "";
        
        // FIX: Create the Scanner *before* the loop.
        // NOTE: A Scanner on System.in should generally not be closed,
        // as closing it closes System.in for the entire application.
        final Scanner input = new Scanner(System.in); 

        while (!"yes".equals(answer) && !"no".equals(answer)) {
            System.out.println(questionText);
            System.out.print("Please enter: [yes, no] ");

            answer = input.nextLine().toLowerCase().trim(); // Convert to lowercase and trim for better input handling

            switch (answer) {
                case "yes":
                    yesCallback.run();
                    break;
                case "no":
                    noCallback.run();
                    break;
                // No 'default' case is needed since the 'while' loop handles invalid input.
            }
        }
    }
}
