package com.yiban.rec.service;

import javax.validation.GroupSequence;

public interface GroupBy {
	public interface GroupA {};
	public interface GroupB {};
	public interface GroupC {};
	public interface GroupD {};
	@GroupSequence({GroupA.class,GroupB.class,GroupC.class,GroupD.class})
	public interface Group {};
	public interface LoginReg {};
	public interface RegisterReg {};
}
