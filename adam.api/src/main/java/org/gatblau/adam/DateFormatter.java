/**
 * Copyright (c) 2015 GATBLAU - www.gatblau.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gatblau.adam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A thread safe date formatter used to convert Date to String and vice versa.
 * The formatter is used to pass dates as query parameters in HTTP calls.
 * This class wraps SimpleDateFormat to provide thread safety.
 */
public class DateFormatter {
    private ThreadLocal<DateFormat> formatter;

    public DateFormatter() {
        this("dd-MM-yyyy-HH:mm");
    }

    public DateFormatter(String formatString){
       this.formatter = new ThreadLocal<DateFormat>() {
            @Override
            public java.text.DateFormat get() {
                return super.get();
            }

            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(formatString);
            }

            @Override
            public void remove() {
                super.remove();
            }

            @Override
            public void set(DateFormat value) {
                super.set(value);
            }
        };
    }

    /**
     * Converts a specified Date to a formatted String.
     * @param date the date to format.
     * @return a String containing the formatted date.
     */
    public String toString(Date date) {
        return formatter.get().format(date);
    }

    /**
     * Converts a specified formatted String containing date and time information
     * into a Date object.
     * @param dateString the date String following format "dd-MM-yyyy-HH:mm"
     * @return an instance of Date.
     * @throws ParseException thrown if the specified String is not formatted in the right way.
     */
    public Date fromString(String dateString) throws ParseException {
        return formatter.get().parse(dateString);
    }
}
