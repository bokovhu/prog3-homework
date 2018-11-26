/*
 *     Chatter - my Programming III. homework assignment
 *     Copyright (C) 2018  Botond János Kovács
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.bokov.prog3.util;

import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
public class I18N {

    private static final String RESOURCE_BUNDLE_BASENAME = "me.bokov.prog3.i18n.ui";
    @Inject
    private Logger logger;
    private ResourceBundle resourceBundle;

    public void load(Locale locale) {

        logger.info("Loading language in language {} from resource bundle {}", locale.getLanguage(), RESOURCE_BUNDLE_BASENAME);

        resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASENAME, locale);

    }

    public String getText(String key, Object... args) {

        if (resourceBundle.containsKey(key)) {

            String format = resourceBundle.getString(key);

            if (args != null && args.length > 0) {
                try {
                    return MessageFormat.format(format, args);
                } catch (IllegalArgumentException iae) {
                    return "??{illegal-format}??" + format;
                }
            }

            return format;

        } else {

            return "???" + key + "???";

        }

    }

}
