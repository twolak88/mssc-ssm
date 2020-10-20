package twolak.springframework.msscssm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import twolak.springframework.msscssm.domain.Payment;
import twolak.springframework.msscssm.domain.PaymentEvent;
import twolak.springframework.msscssm.domain.PaymentState;
import twolak.springframework.msscssm.repositories.PaymentRepository;

/**
 *
 * @author twolak
 */
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    
    @Override
    public Payment newPayment(Payment payment) {
        payment.setPaymentState(PaymentState.NEW);
        return this.paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        //TODO implement
        return stateMachine;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        //TODO implement
        return stateMachine;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        //TODO implement
        return stateMachine;
    }
    
    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = this.paymentRepository.getOne(paymentId);
        
        StateMachine<PaymentState, PaymentEvent> stateMachine = this.stateMachineFactory.getStateMachine(Long.toString(payment.getId()));
        stateMachine.stop();
        
        stateMachine.getStateMachineAccessor().doWithAllRegions(stateMachineAccessor -> {
            stateMachineAccessor.resetStateMachine(new DefaultStateMachineContext<>(payment.getPaymentState(), null, null, null));
        });
        stateMachine.start();
        return stateMachine;
    }
    
}
