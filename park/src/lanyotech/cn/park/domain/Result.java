package lanyotech.cn.park.domain;

import lanyotech.cn.park.protoc.ResponseProtoc.Response;

public class Result <T>{
	public static final int state_success=0;
	public static final int state_fail=1;
	public static final int state_timeout=2;
	public static final int state_ununited=3;
	
	public int state;
	public Response data;
	public T t;
}
