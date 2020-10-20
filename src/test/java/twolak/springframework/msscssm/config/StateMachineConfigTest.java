package twolak.springframework.msscssm.config;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import twolak.springframework.msscssm.domain.PaymentEvent;
import twolak.springframework.msscssm.domain.PaymentState;

/**
 *
 * @author twolak
 */
@Slf4j
@SpringBootTest
public class StateMachineConfigTest {
    
    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    
    @Test
    public void testNewStateMachine() throws Exception {
        StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        
        Assertions.assertEquals(PaymentState.NEW, stateMachine.getState().getId());
        
        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        
        Assertions.assertEquals(PaymentState.NEW, stateMachine.getState().getId());
        
        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        
        Assertions.assertEquals(PaymentState.PRE_AUTH, stateMachine.getState().getId());
    }
    
    @Test
    public void testNewStateMachinePreAuthDeclined() throws Exception {
        StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine(UUID.randomUUID());
        stateMachine.start();
        
        Assertions.assertEquals(PaymentState.NEW, stateMachine.getState().getId());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        
        Assertions.assertEquals(PaymentState.NEW, stateMachine.getState().getId());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_DECLINED);
        
        Assertions.assertEquals(PaymentState.PRE_AUTH_ERROR, stateMachine.getState().getId());
    }
}
