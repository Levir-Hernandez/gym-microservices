package com.crm.gym.api;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeTags("unit")
@SelectPackages("com.crm.gym.api")
public class UnitTestSuite
{

}