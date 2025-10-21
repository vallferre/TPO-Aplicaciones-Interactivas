package com.uade.tpo.marketplace.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.repository.FavoriteRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl; // URL de tu frontend para link al producto

    @Override
    public void notifyUsersByProductId(Long productId) {
        // Obtener IDs de los usuarios que tienen el producto como favorito
        List<Long> userIds = favoriteRepository.findUserIdsByProductId(productId);

        Optional<Product> productoOpt = productRepository.findById(productId);
        if (productoOpt.isEmpty()) return;

        Product producto = productoOpt.get();

        // Iterar y enviar mail a cada usuario
        for (Long userId : userIds) {
            userRepository.findById(userId).ifPresent(user -> {
                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

                    helper.setTo(user.getEmail());
                    helper.setFrom(from);
                    helper.setSubject("¡" + producto.getName() + " ha sido actualizado!");

                    // HTML personalizado
                    String htmlMsg = "<!DOCTYPE html>"
                            + "<html>"
                            + "<body style='font-family:Arial,sans-serif; color:#333;'>"
                            + "<h2>Hola " + user.getName() + " 👋</h2>"
                            + "<p>El producto <strong>" + producto.getName() + "</strong> que tenés en tus favoritos ha sido actualizado.</p>"
                            + "<p>Ahora podés revisar sus cambios y novedades:</p>"
                            + "<a href='" + frontendUrl + "/product/" + producto.getId() + "' "
                            + "style='display:inline-block; padding:10px 20px; background-color:#007bff; color:white; text-decoration:none; border-radius:5px;'>"
                            + "Ver Producto</a>"
                            + "<p>¡No te pierdas las novedades!</p>"
                            + "<hr>"
                            + "<p style='font-size:0.85em; color:#666;'>"
                            + "Este es un mensaje automático de Marketplace. Por favor, no respondas a este correo."
                            + "</p>"
                            + "</body>"
                            + "</html>";

                    helper.setText(htmlMsg, true);
                    mailSender.send(message);
                    System.out.println("Mail enviado a: " + user.getEmail());
                } catch (Exception e) {
                    System.err.println("Error enviando mail a: " + user.getEmail());
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void sendWelcomeNotification(String email, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(from);
        message.setSubject("¡Bienvenido a nuestra tienda!");
        message.setText("Hola " + username + ",\n\n"
                + "¡Gracias por registrarte en nuestro marketplace! 🎉\n"
                + "A partir de ahora podrás explorar nuestra comunidad, agregar tus favoritos y mucho más.\n\n"
                + "¡Nos alegra tenerte con nosotros!\n\n"
                + "El equipo de Replicaria 🛒");
        mailSender.send(message);
    }
}
