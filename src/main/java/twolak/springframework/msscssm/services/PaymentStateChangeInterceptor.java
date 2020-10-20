package twolak.springframework.msscssm.services;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import twolak.springframework.msscssm.domain.Payment;
import twolak.springframework.msscssm.domain.PaymentEvent;
import twolak.springframework.msscssm.domain.PaymentState;
import twolak.springframework.msscssm.repositories.PaymentRepository;

/**
 *
 * @author twolak
 */
@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, 
            Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine) {
        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)))
                    .ifPresent(paymentId -> {
                        Payment payment = this.paymentRepository.getOne(paymentId);
                        payment.setPaymentState(state.getId());
                        this.paymentRepository.save(payment);
                    });
        });
    }
}
