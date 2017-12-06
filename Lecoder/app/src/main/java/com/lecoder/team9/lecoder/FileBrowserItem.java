package com.lecoder.team9.lecoder;

/**
 * Created by leejinwone on 2017-12-06.
 */


    public class FileBrowserItem {
        int icons ;
        String titleStr ;
        String descStr ;

        public FileBrowserItem(String titleStr, String descStr, int icons){
            this.titleStr = titleStr;
            this.descStr = descStr;
            this.icons = icons;
        }
        public void setIcon(int icon) {
            this.icons = icon ;
        }
        public void setTitle(String title) {
            this.titleStr = title ;
        }
        public void setDesc(String desc) {
            this.descStr = desc ;
        }

        public int getIcon() {
            return this.icons ;
        }
        public String getTitle() {
            return this.titleStr ;
        }
        public String getDesc() {
            return this.descStr ;
        }
    }
