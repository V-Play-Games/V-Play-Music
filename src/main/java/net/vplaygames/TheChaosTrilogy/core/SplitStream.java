/*
 * Copyright 2020-2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vplaygames.TheChaosTrilogy.core;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class SplitStream extends PrintStream {
    private final PrintStream p1;
    private final PrintStream p2;

    public SplitStream(PrintStream p1, PrintStream p2) {
        super(p2);
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void flush() {
        p1.flush();
        p2.flush();
    }

    @Override
    public void close() {
        super.close();
        p1.close();
    }

    @Override
    public void write(int b) {
        p1.write(b);
        p2.write(b);
    }

    @Override
    public void write(@NotNull byte[] buf, int off, int len) {
        p1.write(buf, off, len);
        p2.write(buf, off, len);
    }

    @Override
    public void print(String s) {
        p1.print(s.replaceAll("\n", "\r\n"));
        p2.print(s.replaceAll("\n", "\r\n"));
    }

    @Override
    public void println(String x) {
        p1.println(x.replaceAll("\n", "\r\n"));
        p2.println(x.replaceAll("\n", "\r\n"));
    }
}
