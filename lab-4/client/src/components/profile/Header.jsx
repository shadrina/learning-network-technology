import React from 'react'
import { Link } from 'react-router-dom';

import Navbar from 'react-bootstrap/lib/Navbar'
import Nav from 'react-bootstrap/es/Nav'
import NavItem from 'react-bootstrap/es/NavItem'

import '../../../static/styles/bootstrap.css'


export default class Header extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Navbar>
                <Navbar.Header>
                    <Navbar.Brand>
                        <a href='/'>Студенческий чатик</a>
                    </Navbar.Brand>
                    <Navbar.Toggle />
                </Navbar.Header>
                <Nav bsStyle="tabs">
                    <NavItem
                        componentClass={Link}
                        href="/chat"
                        to="/chat"
                        active={location.pathname === '/chat'}
                        eventKey={1}>
                        REST Chat
                    </NavItem>
                    <NavItem
                        componentClass={Link}
                        href="/coming-soon"
                        to="/coming-soon"
                        active={location.pathname === '/coming-soon'}
                        eventKey={2}>
                        Coming soon...
                    </NavItem>
                </Nav>
            </Navbar>
        );
    }
}
