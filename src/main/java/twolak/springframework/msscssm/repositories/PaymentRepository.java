package twolak.springframework.msscssm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import twolak.springframework.msscssm.domain.Payment;

/**
 *
 * @author twolak
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
}
