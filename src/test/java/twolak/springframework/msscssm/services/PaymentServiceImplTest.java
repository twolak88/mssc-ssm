package twolak.springframework.msscssm.services;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;
import twolak.springframework.msscssm.domain.Payment;
import twolak.springframework.msscssm.domain.PaymentEvent;
import twolak.springframework.msscssm.domain.PaymentState;
import twolak.springframework.msscssm.repositories.PaymentRepository;

/**
 *
 * @author twolak
 */
@Slf4j
@SpringBootTest
public class PaymentServiceImplTest {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    private Payment payment;
    
    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(BigDecimal.valueOf(12.99)).build();
    }
    
    @Transactional
    @Test
    public void testPreAuth() {
        Payment savedPayment = this.paymentService.newPayment(payment);
        
        Assertions.assertEquals(PaymentState.NEW, savedPayment.getPaymentState());
        
        StateMachine<PaymentState, PaymentEvent> stateMachine = this.paymentService.preAuth(savedPayment.getId());
        
        Payment preAuthedPayment = this.paymentRepository.getOne(savedPayment.getId());
        
        log.debug(preAuthedPayment.toString());
        log.debug(preAuthedPayment.getPaymentState().toString());
        
        MatcherAssert.assertThat(stateMachine.getState().getId(), 
                CoreMatchers.anyOf(CoreMatchers.is(PaymentState.PRE_AUTH), CoreMatchers.is(PaymentState.PRE_AUTH_ERROR)));
        MatcherAssert.assertThat(preAuthedPayment.getPaymentState(),
                CoreMatchers.anyOf(CoreMatchers.is(PaymentState.PRE_AUTH), CoreMatchers.is(PaymentState.PRE_AUTH_ERROR)));
        
    }
    
    @Transactional
    @RepeatedTest(10)
    public void testAuth() {
        Payment savedPayment = this.paymentService.newPayment(payment);
        StateMachine<PaymentState, PaymentEvent> preAuthStateMachine = this.paymentService.preAuth(savedPayment.getId());
        
        if(preAuthStateMachine.getState().getId() == PaymentState.PRE_AUTH) {
            log.debug("PreAuthorized");
            StateMachine<PaymentState, PaymentEvent> authStateMachine = this.paymentService.authorizePayment(savedPayment.getId());
            
            Payment authedPayment = this.paymentRepository.getOne(savedPayment.getId());
            
            MatcherAssert.assertThat(authStateMachine.getState().getId(),
                    CoreMatchers.anyOf(CoreMatchers.is(PaymentState.AUTH), CoreMatchers.is(PaymentState.AUTH_ERROR)));
            MatcherAssert.assertThat(authedPayment.getPaymentState(),
                    CoreMatchers.anyOf(CoreMatchers.is(PaymentState.AUTH), CoreMatchers.is(PaymentState.AUTH_ERROR)));
        } else {
            log.debug("PreAuthorize declined");
            Payment preAuthedPayment = this.paymentRepository.getOne(savedPayment.getId());
            
            Assertions.assertEquals(PaymentState.PRE_AUTH_ERROR, preAuthStateMachine.getState().getId());
            Assertions.assertEquals(PaymentState.PRE_AUTH_ERROR, preAuthedPayment.getPaymentState());
        }
        
    }
    
//    @Test
//    public void testNewPayment() {
//    }
//    
//    @Test
//    public void testAuthorizePayment() {
//    }
//    
//    @Test
//    public void testDeclineAuth() {
//    }
    
}
