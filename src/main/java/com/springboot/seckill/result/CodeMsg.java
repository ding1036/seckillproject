package com.springboot.seckill.result;

public class CodeMsg {
	
	private int code;
	private String msg;
	
	//error code
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "server error");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "param error：%s");
	//login model 5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session do not exist");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "password can not be null");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "phone is null");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "phone is not valid");
	public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "phone not exist");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "wrong password");
	
	//list model 5003XX
	
	//order model 5004XX
	
	//seckill model 5005XX
	public static CodeMsg SECKILL_OVER = new CodeMsg(500500, "seckill over");
	public static CodeMsg REPEATE_SECKILL = new CodeMsg(500501, "can not repeate seckill");
	
	private CodeMsg( ) {
	}
			
	private CodeMsg( int code,String msg ) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + "]";
	}
	
	
}
