package com.bootcamp.demo.core.services.Impl;

import com.bootcamp.demo.core.services.DefaultBlogsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeDefinition;

@Component(service = DefaultBlogsService.class, immediate = true)
@Designate(ocd = DefaultBlogsServiceImpl.Config.class)
public class DefaultBlogsServiceImpl implements DefaultBlogsService {

    @ObjectClassDefinition(name = "Default Blogs Service Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Default Blogs", description = "Maximum number of blogs to display")
        int default_blogs() default 5;
    }

    private int default_blogs;

    @Activate
    @Modified
    protected void activate(final Config config) {
        this.default_blogs = config.default_blogs();
    }

    @Override
    public int getDefault_blogs() {
        return this.default_blogs;
    }
}
