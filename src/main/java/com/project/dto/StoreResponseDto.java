package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponseDto {

    @Schema(description = "Unique store id",example = "2a3c8b0-a25s-g54ea34-sfds2")
    private UUID id;

    @Schema(description = "Store name",example = "Fruit Shop")
    private String name;

    @Schema(description = "URL-Friendly store slug",example = "fruit-shop")
    private String slug;

    @Schema(description = "Store owner user identifier",example = "s2a3c4z3w4a-a23a-23d23zsa-21w1a")
    private UUID ownerId;

}
