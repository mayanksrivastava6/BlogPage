package com.bootcamp.demo.core.models;

import com.bootcamp.demo.core.models.Impl.ArchiveModelImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Archive {
    public String getParentPath();
    public Map<String, ArchiveModelImpl.ArchiveDate> getFormattedDates();
}
