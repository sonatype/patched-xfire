package org.codehaus.xfire.handler;

/**
 * @author Arjen Poutsma
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import junit.framework.TestCase;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.soap.handler.SoapBodyHandler;

public class HandlerPipelineTest
    extends TestCase
{
    private HandlerPipeline handlerPipeline;

    public void testPhases()
        throws Exception
    {
        List phases = new ArrayList();
        phases.add(new Phase(Phase.TRANSPORT, 100));
        phases.add(new Phase(Phase.PARSE, 100));
        phases.add(new Phase(Phase.PRE_DISPATCH, 100));
        phases.add(new Phase(Phase.DISPATCH, 500));
        phases.add(new Phase(Phase.USER, 500));

        handlerPipeline = new HandlerPipeline(phases);

        PhaseHandler handler1 = new PhaseHandler(Phase.TRANSPORT);
        PhaseHandler handler2 = new PhaseHandler(Phase.PARSE);
        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);

        handlerPipeline.invoke(new MessageContext());

        assertTrue(handler1.isInvoked());
        assertTrue(handler2.isInvoked());
    }

    public void testPauseResume()
        throws Exception
    {
        List phases = new ArrayList();
        phases.add(new Phase(Phase.TRANSPORT, 100));
        phases.add(new Phase(Phase.PARSE, 100));
        phases.add(new Phase(Phase.PRE_DISPATCH, 100));
        phases.add(new Phase(Phase.DISPATCH, 500));
        phases.add(new Phase(Phase.USER, 500));

        MessageContext context = new MessageContext();
        handlerPipeline = new HandlerPipeline(phases);
        
        PauseHandler handler1 = new PauseHandler(Phase.TRANSPORT);
        PhaseHandler handler2 = new PhaseHandler(Phase.PARSE);
        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);
        context.setInPipeline(handlerPipeline);
        
        handlerPipeline.invoke(context);
        Stack invoked = (Stack) context.getProperty(handlerPipeline.toString());
        
        assertTrue(handler1.isInvoked());
        assertTrue(invoked.contains(handler1));
        assertFalse(handler2.isInvoked());
        assertFalse(invoked.contains(handler2));
        
        context.getCurrentPipeline().resume(context);
        
        assertTrue(handler1.isInvoked());
        assertTrue(invoked.contains(handler1));
        assertTrue(handler2.isInvoked());
        assertTrue(invoked.contains(handler2));
    }

    public void testSorting()
        throws Exception
    {
        List phases = new ArrayList();
        phases.add(new Phase(Phase.TRANSPORT, 100));

        handlerPipeline = new HandlerPipeline(phases);

        PhaseHandler handler1 = new PhaseHandler(Phase.TRANSPORT);
        PhaseHandler2 handler2 = new PhaseHandler2(Phase.TRANSPORT);
        handler2.before(handler1.getClass().getName());

        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);

        List handlers = handlerPipeline.getPhaseHandlers(Phase.TRANSPORT).getHandlers();

        assertTrue(handlers.get(0) == handler2);
        assertTrue(handlers.get(1) == handler1);

        // try inserting in reverse
        handlerPipeline = new HandlerPipeline(phases);
        handlerPipeline.addHandler(handler2);
        handlerPipeline.addHandler(handler1);

        handlers = handlerPipeline.getPhaseHandlers(Phase.TRANSPORT).getHandlers();

        assertTrue(handlers.get(0) == handler2);
        assertTrue(handlers.get(1) == handler1);

        // try reverse ordering
        handlerPipeline = new HandlerPipeline(phases);

        handler1 = new PhaseHandler(Phase.TRANSPORT);
        handler2 = new PhaseHandler2(Phase.TRANSPORT);
        handler2.after(handler1.getClass().getName());

        handlerPipeline.addHandler(handler1);
        handlerPipeline.addHandler(handler2);

        handlers = handlerPipeline.getPhaseHandlers(Phase.TRANSPORT).getHandlers();

        assertTrue(handlers.get(0) == handler1);
        assertTrue(handlers.get(1) == handler2);
    }

    public void testAdvancedSorting()
    {
        List phases = new ArrayList();
        phases.add(new Phase(Phase.TRANSPORT, 100));
        phases.add(new Phase(Phase.PARSE, 100));
        phases.add(new Phase(Phase.PRE_DISPATCH, 100));
        phases.add(new Phase(Phase.DISPATCH, 500));
        phases.add(new Phase(Phase.USER, 500));

        handlerPipeline = new HandlerPipeline(phases);

        handlerPipeline.addHandler(new DispatchServiceHandler());
        handlerPipeline.addHandler(new LocateBindingHandler());
        handlerPipeline.addHandler(new SoapBodyHandler());

        List handlers = handlerPipeline.getPhaseHandlers(Phase.DISPATCH).getHandlers();

        assertTrue(handlers.get(0) instanceof LocateBindingHandler);
        assertTrue(handlers.get(1) instanceof SoapBodyHandler);
    }

    public void testInvalidSorting()
        throws Exception
    {
        List phases = new ArrayList();
        phases.add(new Phase(Phase.TRANSPORT, 100));

        handlerPipeline = new HandlerPipeline(phases);

        PhaseHandler handler1 = new PhaseHandler(Phase.TRANSPORT);
        PhaseHandler2 handler2 = new PhaseHandler2(Phase.TRANSPORT);
        handler1.before(handler2.getClass().getName());
        handler2.before(handler1.getClass().getName());

        handlerPipeline.addHandler(handler1);

        try
        {
            handlerPipeline.addHandler(handler2);
            fail("Invalid sort!");
        }
        catch (IllegalStateException e)
        {
        }

        handlerPipeline = new HandlerPipeline(phases);

        handler1 = new PhaseHandler(Phase.TRANSPORT);
        handler2 = new PhaseHandler2(Phase.TRANSPORT);
        handler1.after(handler2.getClass().getName());
        handler2.after(handler1.getClass().getName());

        handlerPipeline.addHandler(handler1);

        try
        {
            handlerPipeline.addHandler(handler2);
            fail("Invalid sort!");
        }
        catch (IllegalStateException e)
        {
        }
    }

    public class PhaseHandler
        extends AbstractHandler
    {
        private boolean invoked = false;

        public PhaseHandler(String phase)
        {
            super();
            setPhase(phase);
        }

        public void invoke(MessageContext context)
            throws Exception
        {
            invoked = true;
        }

        public boolean isInvoked()
        {
            return invoked;
        }

    }

    public class PhaseHandler2
        extends AbstractHandler
    {
        private boolean invoked = false;

        public PhaseHandler2(String phase)
        {
            super();
            setPhase(phase);
        }

        public void invoke(MessageContext context)
            throws Exception
        {
            invoked = true;
        }

        public boolean isInvoked()
        {
            return invoked;
        }

    }

    public class PauseHandler
        extends AbstractHandler
    {
        private boolean invoked = false;

        public PauseHandler(String phase)
        {
            super();
            setPhase(phase);
        }

        public void invoke(MessageContext context)
            throws Exception
        {
            context.getCurrentPipeline().pause();
            invoked = true;
        }

        public boolean isInvoked()
        {
            return invoked;
        }

    }
}