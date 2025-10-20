package com.uade.tpo.marketplace.entity.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.uade.tpo.marketplace.entity.Favorite;
import com.uade.tpo.marketplace.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {

    private String username;
    private List<Long> favoriteProductIds;
    private String message;

    // Constructor usado para listar favoritos
    public FavoriteResponse(User user, List<Favorite> favorites) {
        this.username = user.getUsername();
        this.favoriteProductIds = favorites.stream()
                .map(Favorite::getProductId)
                .collect(Collectors.toList());
        this.message = "Lista de productos favoritos obtenida correctamente";
    }

    // Constructor usado para respuestas simples (agregar o eliminar)
    public FavoriteResponse(Long userId, Long productId, String message) {
        this.username = "user_" + userId;
        this.favoriteProductIds = List.of(productId);
        this.message = message;
    }

    public static FavoriteResponse from(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getUser().getUsername(),
                List.of(favorite.getProductId()),
                "Producto favorito cargado correctamente"
        );
    }
}
