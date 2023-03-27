package hr.tvz.pios.modul.user.settings;

import hr.tvz.pios.modul.user.User;
import java.time.LocalDateTime;

/**
 * Rekord za odgovor za korisničke postavke.
 */
public record UserSettingsResponse(
    String username,
    String email,
    String description,
    LocalDateTime creationDate
) {
  static UserSettingsResponse fromUser(User user) {
    return new UserSettingsResponse(
        user.getUsername(), user.getEmail(), user.getDescription(), user.getCreationDate());
  }
}
