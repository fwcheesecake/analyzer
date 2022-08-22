package com.cheesecake.helpers;

public class Token {
    public String value;
    public String token;
    public String start;
    public String end;

    public Token(String value, String token, String start, String end) {
        this.value = value;
        this.token = token;
        this.start = start;
        this.end = end;
    }
}
