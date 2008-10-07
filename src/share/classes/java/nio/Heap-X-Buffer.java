/*
 * Copyright 2000-2002 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

#warn This file is preprocessed before being compiled

package java.nio;


/**
#if[rw]
 * A read/write Heap$Type$Buffer.
#else[rw]
 * A read-only Heap$Type$Buffer.  This class extends the corresponding
 * read/write class, overriding the mutation methods to throw a {@link
 * ReadOnlyBufferException} and overriding the view-buffer methods to return an
 * instance of this class rather than of the superclass.
#end[rw]
 */

class Heap$Type$Buffer$RW$
    extends {#if[ro]?Heap}$Type$Buffer
{

    // For speed these fields are actually declared in X-Buffer;
    // these declarations are here as documentation
    /*
#if[rw]
    protected final $type$[] hb;
    protected final int offset;
#end[rw]
    */

    Heap$Type$Buffer$RW$(int cap, int lim) {            // package-private
#if[rw]
        super(-1, 0, lim, cap, new $type$[cap], 0);
        /*
        hb = new $type$[cap];
        offset = 0;
        */
#else[rw]
        super(cap, lim);
        this.isReadOnly = true;
#end[rw]
    }

    Heap$Type$Buffer$RW$($type$[] buf, int off, int len) { // package-private
#if[rw]
        super(-1, off, off + len, buf.length, buf, 0);
        /*
        hb = buf;
        offset = 0;
        */
#else[rw]
        super(buf, off, len);
        this.isReadOnly = true;
#end[rw]
    }

    protected Heap$Type$Buffer$RW$($type$[] buf,
                                   int mark, int pos, int lim, int cap,
                                   int off)
    {
#if[rw]
        super(mark, pos, lim, cap, buf, off);
        /*
        hb = buf;
        offset = off;
        */
#else[rw]
        super(buf, mark, pos, lim, cap, off);
        this.isReadOnly = true;
#end[rw]
    }

    public $Type$Buffer slice() {
        return new Heap$Type$Buffer$RW$(hb,
                                        -1,
                                        0,
                                        this.remaining(),
                                        this.remaining(),
                                        this.position() + offset);
    }

    public $Type$Buffer duplicate() {
        return new Heap$Type$Buffer$RW$(hb,
                                        this.markValue(),
                                        this.position(),
                                        this.limit(),
                                        this.capacity(),
                                        offset);
    }

    public $Type$Buffer asReadOnlyBuffer() {
#if[rw]
        return new Heap$Type$BufferR(hb,
                                     this.markValue(),
                                     this.position(),
                                     this.limit(),
                                     this.capacity(),
                                     offset);
#else[rw]
        return duplicate();
#end[rw]
    }

#if[rw]

    protected int ix(int i) {
        return i + offset;
    }

    public $type$ get() {
        return hb[ix(nextGetIndex())];
    }

    public $type$ get(int i) {
        return hb[ix(checkIndex(i))];
    }

    public $Type$Buffer get($type$[] dst, int offset, int length) {
        checkBounds(offset, length, dst.length);
        if (length > remaining())
            throw new BufferUnderflowException();
        System.arraycopy(hb, ix(position()), dst, offset, length);
        position(position() + length);
        return this;
    }

    public boolean isDirect() {
        return false;
    }

#end[rw]

    public boolean isReadOnly() {
        return {#if[rw]?false:true};
    }

    public $Type$Buffer put($type$ x) {
#if[rw]
        hb[ix(nextPutIndex())] = x;
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer put(int i, $type$ x) {
#if[rw]
        hb[ix(checkIndex(i))] = x;
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer put($type$[] src, int offset, int length) {
#if[rw]
        checkBounds(offset, length, src.length);
        if (length > remaining())
            throw new BufferOverflowException();
        System.arraycopy(src, offset, hb, ix(position()), length);
        position(position() + length);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer put($Type$Buffer src) {
#if[rw]
        if (src instanceof Heap$Type$Buffer) {
            if (src == this)
                throw new IllegalArgumentException();
            Heap$Type$Buffer sb = (Heap$Type$Buffer)src;
            int n = sb.remaining();
            if (n > remaining())
                throw new BufferOverflowException();
            System.arraycopy(sb.hb, sb.ix(sb.position()),
                             hb, ix(position()), n);
            sb.position(sb.position() + n);
            position(position() + n);
        } else if (src.isDirect()) {
            int n = src.remaining();
            if (n > remaining())
                throw new BufferOverflowException();
            src.get(hb, ix(position()), n);
            position(position() + n);
        } else {
            super.put(src);
        }
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer compact() {
#if[rw]
        System.arraycopy(hb, ix(position()), hb, ix(0), remaining());
        position(remaining());
        limit(capacity());
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }



#if[byte]

    byte _get(int i) {                          // package-private
        return hb[i];
    }

    void _put(int i, byte b) {                  // package-private
#if[rw]
        hb[i] = b;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    // char

#if[rw]

    public char getChar() {
        return Bits.getChar(this, ix(nextGetIndex(2)), bigEndian);
    }

    public char getChar(int i) {
        return Bits.getChar(this, ix(checkIndex(i, 2)), bigEndian);
    }

#end[rw]

    public $Type$Buffer putChar(char x) {
#if[rw]
        Bits.putChar(this, ix(nextPutIndex(2)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer putChar(int i, char x) {
#if[rw]
        Bits.putChar(this, ix(checkIndex(i, 2)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public CharBuffer asCharBuffer() {
        int size = this.remaining() >> 1;
        int off = offset + position();
        return (bigEndian
                ? (CharBuffer)(new ByteBufferAsCharBuffer$RW$B(this,
                                                               -1,
                                                               0,
                                                               size,
                                                               size,
                                                               off))
                : (CharBuffer)(new ByteBufferAsCharBuffer$RW$L(this,
                                                               -1,
                                                               0,
                                                               size,
                                                               size,
                                                               off)));
    }


    // short

#if[rw]

    public short getShort() {
        return Bits.getShort(this, ix(nextGetIndex(2)), bigEndian);
    }

    public short getShort(int i) {
        return Bits.getShort(this, ix(checkIndex(i, 2)), bigEndian);
    }

#end[rw]

    public $Type$Buffer putShort(short x) {
#if[rw]
        Bits.putShort(this, ix(nextPutIndex(2)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer putShort(int i, short x) {
#if[rw]
        Bits.putShort(this, ix(checkIndex(i, 2)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public ShortBuffer asShortBuffer() {
        int size = this.remaining() >> 1;
        int off = offset + position();
        return (bigEndian
                ? (ShortBuffer)(new ByteBufferAsShortBuffer$RW$B(this,
                                                                 -1,
                                                                 0,
                                                                 size,
                                                                 size,
                                                                 off))
                : (ShortBuffer)(new ByteBufferAsShortBuffer$RW$L(this,
                                                                 -1,
                                                                 0,
                                                                 size,
                                                                 size,
                                                                 off)));
    }


    // int

#if[rw]

    public int getInt() {
        return Bits.getInt(this, ix(nextGetIndex(4)), bigEndian);
    }

    public int getInt(int i) {
        return Bits.getInt(this, ix(checkIndex(i, 4)), bigEndian);
    }

#end[rw]

    public $Type$Buffer putInt(int x) {
#if[rw]
        Bits.putInt(this, ix(nextPutIndex(4)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer putInt(int i, int x) {
#if[rw]
        Bits.putInt(this, ix(checkIndex(i, 4)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public IntBuffer asIntBuffer() {
        int size = this.remaining() >> 2;
        int off = offset + position();
        return (bigEndian
                ? (IntBuffer)(new ByteBufferAsIntBuffer$RW$B(this,
                                                             -1,
                                                             0,
                                                             size,
                                                             size,
                                                             off))
                : (IntBuffer)(new ByteBufferAsIntBuffer$RW$L(this,
                                                             -1,
                                                             0,
                                                             size,
                                                             size,
                                                             off)));
    }


    // long

#if[rw]

    public long getLong() {
        return Bits.getLong(this, ix(nextGetIndex(8)), bigEndian);
    }

    public long getLong(int i) {
        return Bits.getLong(this, ix(checkIndex(i, 8)), bigEndian);
    }

#end[rw]

    public $Type$Buffer putLong(long x) {
#if[rw]
        Bits.putLong(this, ix(nextPutIndex(8)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer putLong(int i, long x) {
#if[rw]
        Bits.putLong(this, ix(checkIndex(i, 8)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public LongBuffer asLongBuffer() {
        int size = this.remaining() >> 3;
        int off = offset + position();
        return (bigEndian
                ? (LongBuffer)(new ByteBufferAsLongBuffer$RW$B(this,
                                                               -1,
                                                               0,
                                                               size,
                                                               size,
                                                               off))
                : (LongBuffer)(new ByteBufferAsLongBuffer$RW$L(this,
                                                               -1,
                                                               0,
                                                               size,
                                                               size,
                                                               off)));
    }


    // float

#if[rw]

    public float getFloat() {
        return Bits.getFloat(this, ix(nextGetIndex(4)), bigEndian);
    }

    public float getFloat(int i) {
        return Bits.getFloat(this, ix(checkIndex(i, 4)), bigEndian);
    }

#end[rw]

    public $Type$Buffer putFloat(float x) {
#if[rw]
        Bits.putFloat(this, ix(nextPutIndex(4)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer putFloat(int i, float x) {
#if[rw]
        Bits.putFloat(this, ix(checkIndex(i, 4)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public FloatBuffer asFloatBuffer() {
        int size = this.remaining() >> 2;
        int off = offset + position();
        return (bigEndian
                ? (FloatBuffer)(new ByteBufferAsFloatBuffer$RW$B(this,
                                                                 -1,
                                                                 0,
                                                                 size,
                                                                 size,
                                                                 off))
                : (FloatBuffer)(new ByteBufferAsFloatBuffer$RW$L(this,
                                                                 -1,
                                                                 0,
                                                                 size,
                                                                 size,
                                                                 off)));
    }


    // double

#if[rw]

    public double getDouble() {
        return Bits.getDouble(this, ix(nextGetIndex(8)), bigEndian);
    }

    public double getDouble(int i) {
        return Bits.getDouble(this, ix(checkIndex(i, 8)), bigEndian);
    }

#end[rw]

    public $Type$Buffer putDouble(double x) {
#if[rw]
        Bits.putDouble(this, ix(nextPutIndex(8)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public $Type$Buffer putDouble(int i, double x) {
#if[rw]
        Bits.putDouble(this, ix(checkIndex(i, 8)), x, bigEndian);
        return this;
#else[rw]
        throw new ReadOnlyBufferException();
#end[rw]
    }

    public DoubleBuffer asDoubleBuffer() {
        int size = this.remaining() >> 3;
        int off = offset + position();
        return (bigEndian
                ? (DoubleBuffer)(new ByteBufferAsDoubleBuffer$RW$B(this,
                                                                   -1,
                                                                   0,
                                                                   size,
                                                                   size,
                                                                   off))
                : (DoubleBuffer)(new ByteBufferAsDoubleBuffer$RW$L(this,
                                                                   -1,
                                                                   0,
                                                                   size,
                                                                   size,
                                                                   off)));
    }


#end[byte]


#if[char]

    String toString(int start, int end) {               // package-private
        try {
            return new String(hb, start + offset, end - start);
        } catch (StringIndexOutOfBoundsException x) {
            throw new IndexOutOfBoundsException();
        }
    }


    // --- Methods to support CharSequence ---

    public CharBuffer subSequence(int start, int end) {
        if ((start < 0)
            || (end > length())
            || (start > end))
            throw new IndexOutOfBoundsException();
        int len = end - start;
        return new HeapCharBuffer$RW$(hb,
                                      -1, 0, len, len,
                                      offset + position() + start);
    }

#end[char]


#if[!byte]

    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

#end[!byte]

}
