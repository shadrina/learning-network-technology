import React from 'react'
import axios from 'axios'

import Modal from 'react-bootstrap/es/Modal'
import Form from 'react-bootstrap/es/Form'
import FormGroup from 'react-bootstrap/es/FormGroup'
import Col from 'react-bootstrap/es/Col'
import Button from 'react-bootstrap/es/Button'
import ControlLabel from 'react-bootstrap/es/ControlLabel'
import FormControl from 'react-bootstrap/es/FormControl'

export default class AuthModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            login: null,
            groupId: null
        }
    }

    onLoginChange(e) {
        this.setState({
            login: e.target.value
        });
    }

    onGroupIdChange(e) {
        this.setState({
            groupId: e.target.value
        });
    }

    onClick() {
        let data = {
            'login': this.state.login,
            'groupId': this.state.groupId
        };
        axios.post("/login", data)
            .then(response => {
                this.props.onSuccess(response.data);
            })
            .catch(error => console.error(error))
    }

    render() {
        return (
            <Modal
                show={this.props.show}
                dialogClassName="custom-modal"
            >
                <Modal.Header>
                    <Modal.Title id="contained-modal-title-lg">
                        Залогиньтесь
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form horizontal>
                        <FormGroup controlId="formHorizontalEmail">
                            <Col componentClass={ControlLabel} sm={2}>
                                Логин
                            </Col>
                            <Col sm={10}>
                                <FormControl
                                    type="text"
                                    placeholder="Login..."
                                    onChange={this.onLoginChange.bind(this)}
                                />
                            </Col>
                        </FormGroup>

                        <FormGroup controlId="formHorizontalEmail">
                            <Col componentClass={ControlLabel} sm={2}>
                                Группа
                            </Col>
                            <Col sm={10}>
                                <FormControl
                                    type="text"
                                    placeholder="Group id..."
                                    onChange={this.onGroupIdChange.bind(this)}
                                />
                            </Col>
                        </FormGroup>

                        <FormGroup>
                            <Col smOffset={2} sm={10}>
                                <Button onClick={this.onClick.bind(this)}>
                                    Войти
                                </Button>
                            </Col>
                        </FormGroup>
                    </Form>
                </Modal.Body>
            </Modal>
        );
    }
}
