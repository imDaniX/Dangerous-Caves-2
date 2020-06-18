/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
   All the code is inspired by Boann's answer on https://stackoverflow.com/questions/3422673
   You can use this code however you want to

   Shorted version of https://github.com/imDaniX/EzMath/blob/master/src/me/imdanix/math/FormulasEvaluator.java
*/
public class FormulasEvaluator {
    private static final Expression ZERO = () -> 0;

    private final Expression expression;
    private final Map<String, Double> variables;

    public FormulasEvaluator(String expression) {
        this.variables = new HashMap<>();
        this.expression = thirdImportance(new PointerHolder(expression.replace(" ", "").toLowerCase(Locale.ENGLISH)));
    }

    public void setVariable(String variable, Double value) {
        variables.put(variable, value);
    }

    public double eval() {
        return expression.eval();
    }

    private Expression thirdImportance(PointerHolder holder) {
        Expression x = secondImportance(holder);
        while(true) {
            if(holder.tryNext('+')) {
                Expression a = x;
                Expression b = secondImportance(holder);
                x = () -> a.eval() + b.eval();
            } else
            if(holder.tryNext('-')) {
                Expression a = x;
                Expression b = secondImportance(holder);
                x = () -> a.eval() - b.eval();
            } else
                return x;
        }
    }

    private Expression secondImportance(PointerHolder holder) {
        Expression x = firstImportance(holder);
        while(true) {
            if(holder.tryNext('*')) {
                Expression a = x;
                Expression b = firstImportance(holder);
                x = () -> a.eval() * b.eval();
            } else
            if(holder.tryNext('/')) {
                Expression a = x;
                Expression b = firstImportance(holder);
                x = () -> a.eval() / b.eval();
            } else
            if(holder.tryNext('%')) {
                Expression a = x;
                Expression b = firstImportance(holder);
                x = () -> a.eval() % b.eval();
            } else
                return x;
        }
    }

    private Expression firstImportance(PointerHolder holder) {
        if(holder.tryNext('-')) { // "-5", "--5"..
            Expression a = firstImportance(holder);
            return () -> -a.eval();
        }
        if(holder.tryNext('+')) // "+5", "++5"..
            return firstImportance(holder);
        Expression x = ZERO;
        int start = holder.pointer;
        if(holder.tryNext('(')) {
            x = thirdImportance(holder);
            holder.tryNext(')');
        } else if(isNumberChar(holder.current())) {
            while(isNumberChar(holder.current())) holder.pointer++;
            double a = Double.parseDouble(holder.substring(start, holder.pointer));
            x = () -> a;
        } else if(isWordChar(holder.current())) {
            holder.pointer++;
            while(isWordChar(holder.current()) || isNumberChar(holder.current())) holder.pointer++;
            String str = holder.substring(start, holder.pointer);
            x = () -> variables.get(str);
        }

        if(holder.tryNext('^')) {
            Expression a = x;
            Expression b = firstImportance(holder);
            x = () -> Math.pow(a.eval(), b.eval());
        }
        return x;
    }

    @FunctionalInterface
    private interface Expression {
        double eval();
    }

    /**
     * Used in parse process to unload origin and pointer itself after ending of parse
     * Because of this class everything looks a bit more sh!tty... but still readable
     */
    private static final class PointerHolder {
        private final String origin;
        private int pointer;

        private PointerHolder(String origin) {
            this.origin = origin;
            this.pointer = 0;
        }

        private String substring(int start, int end) {
            return origin.substring(start, end);
        }

        private char current() {
            return origin.length() > pointer ? origin.charAt(pointer) : ' ';
        }

        private boolean tryNext(char c) {
            if(current() == c) {
                pointer++;
                return true;
            }
            return false;
        }
    }

    private static boolean isNumberChar(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

    private static boolean isWordChar(char c) {
        return (c >= 'a' && c <= 'z');
    }
}
