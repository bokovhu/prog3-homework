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

package me.bokov.prog3.cdi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * This class is a CDI producer class. It's single method is used by CDI to provide Logger implementations to CDI beans
 * that use @Inject for a Logger field.
 */
public class LoggerProducer {

    /**
     * Produces a single Logger (SLF4J), and uses the injectionPoint's getBeanClass () if available, or the injected
     * member's declaring class otherwise as the parameter for the LoggerFactory.getLogger () method call.
     * <p>
     * If neither of these are available in the injectionPoint, a Logger with the name "UNKNOWN" is returned.
     *
     * @param injectionPoint the InjectionPoint
     * @return the created Logger object
     */
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {

        Class<?> clazz = null;

        if (injectionPoint.getBean() != null) clazz = injectionPoint.getBean().getBeanClass();
        else if (injectionPoint.getMember() != null) clazz = injectionPoint.getMember().getDeclaringClass();

        if (clazz == null) return LoggerFactory.getLogger("UNKNOWN");
        else return LoggerFactory.getLogger(clazz);

    }

}
