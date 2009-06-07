package com.threelevers.css;

class NthExpression {
    final int a;
    final int b;

    NthExpression(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public boolean matches(int elementNo) {
        if (a == 0) {
            return elementNo == b;
        }
        if ((elementNo - b) % a != 0) {
            return false;
        }
        return (elementNo - b) / a >= 0;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + a;
        result = prime * result + b;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NthExpression other = (NthExpression) obj;
        if (a != other.a) {
            return false;
        }
        if (b != other.b) {
            return false;
        }
        return true;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (a != 0) {
            if (a == -1) {
                sb.append('-');
            } else if (a != 1) {
                sb.append(a);
            }
            sb.append('n');
            if (b > 0) {
                sb.append('+');
            }
        }
        if (b != 0) {
            sb.append(b);
        }
        return sb.toString();
    }
}
