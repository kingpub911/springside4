/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.utils.base;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.Test;

public class ExceptionUtilTest {

	@Test
	public void unchecked() {
		// convert Exception to RuntimeException with cause
		Exception exception = new Exception("my exception");
		RuntimeException runtimeException = ExceptionUtil.unchecked(exception);
		assertThat(runtimeException.getCause()).isEqualTo(exception);

		// do nothing of RuntimeException
		RuntimeException runtimeException2 = ExceptionUtil.unchecked(runtimeException);
		assertThat(runtimeException2).isSameAs(runtimeException);
	}

	@Test
	public void getStackTraceAsString() {
		Exception exception = new Exception("my exception");
		RuntimeException runtimeException = new RuntimeException(exception);

		String stack = ExceptionUtil.stackTraceText(runtimeException);
		System.out.println(stack);
	}

	@Test
	public void isCausedBy() {
		IOException ioexception = new IOException("my exception");
		IllegalStateException illegalStateException = new IllegalStateException(ioexception);
		RuntimeException runtimeException = new RuntimeException(illegalStateException);

		assertThat(ExceptionUtil.isCausedBy(runtimeException, IOException.class)).isTrue();
		assertThat(ExceptionUtil.isCausedBy(runtimeException, IllegalStateException.class, IOException.class)).isTrue();
		assertThat(ExceptionUtil.isCausedBy(runtimeException, Exception.class)).isTrue();
		assertThat(ExceptionUtil.isCausedBy(runtimeException, IllegalAccessException.class)).isFalse();
	}

}
