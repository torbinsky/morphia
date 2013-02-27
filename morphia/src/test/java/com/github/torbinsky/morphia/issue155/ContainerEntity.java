package com.github.torbinsky.morphia.issue155;

import com.github.torbinsky.morphia.testutil.TestEntity;

class ContainerEntity extends TestEntity {
    private static final long serialVersionUID = 1L;
    
    Bar foo = EnumBehindAnInterface.A;
}