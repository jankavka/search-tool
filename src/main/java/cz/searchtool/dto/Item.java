package cz.searchtool.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Item implements
        Serializable {


    String title;
    String link;
    String snippet;



}
