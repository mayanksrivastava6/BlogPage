package com.bootcamp.demo.core.models;

import java.util.List;

public interface PublishBlog {
    List<Blogs> getBlogList();
    public class Blogs {

        private final String heading;        // The title of the blog
        private final String subHeading;     // A short description or subheading of the blog
        private final String formattedDate;  // The date the blog was created, in a formatted string
        private final String image;          // The image URL for the blog
        private final String link;           // The link to the blog post

        // Constructor
        public Blogs(String heading, String subHeading, String formattedDate, String image, String link) {
            this.heading = heading;
            this.subHeading = subHeading;
            this.formattedDate = formattedDate;
            this.image = image;
            this.link = link;
        }

        // Getters for each field
        public String getHeading() {
            return heading;
        }

        public String getSubHeading() {
            return subHeading;
        }

        public String getFormattedDate() {
            return formattedDate;
        }

        public String getImage() {
            return image;
        }

        public String getLink() {
            return link;
        }
    }
}
