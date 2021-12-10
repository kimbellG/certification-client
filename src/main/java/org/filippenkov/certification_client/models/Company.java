package org.filippenkov.certification_client.models;

public class Company {
        private Long id;
        private String name;
        private String unp;
        private String director;
        private String email;
        private String phone;

        public Company(String name, String unp, String director, String email, String phone) {
            this.name = name;
            this.unp = unp;
            this.director = director;
            this.email = email;
            this.phone = phone;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDirector() {
            return director;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getUnp() {
            return unp;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setUnp(String unp) {
            this.unp = unp;
        }
}
