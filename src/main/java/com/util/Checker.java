package com.util;

import com.entity.Host;
import com.entity.ResultOfCheck;

public interface Checker {

	ResultOfCheck check(Host host);
	
}
