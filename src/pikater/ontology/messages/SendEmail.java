package pikater.ontology.messages;

import pikater.agents.utility.MailAgent.EmailType;
import jade.content.AgentAction;

/** Reprezentuje pozadavek na zaslani e-mailu urciteho druhu. */
public class SendEmail implements AgentAction {
    private static final long serialVersionUID = 1437282103218542597L;

    private String email_type;
    private String to_address;
    
    public SendEmail() {
        // prazdny konstruktor pro JADE
    }
    
    public SendEmail(EmailType email_type, String to) {
        this.email_type = email_type.name();
        this.to_address = to;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }

    public String getEmail_type() {
        return email_type;
    }

    public void setEmail_type(String email_type) {
        this.email_type = email_type;
    }
}