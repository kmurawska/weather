import React    from "react";
import ReactDOM from "react-dom";

class App extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.renderAxis();
    }

    renderAxis() {
        var node = this.refs.axis;
        var axis = d3.svg.axis().orient(this.props.orient).ticks(5).scale(this.props.scale);
        d3.select(node).call(axis);
    }

    render() {
        return (
            <div className="panel panel-body">
                :)
            </div>
        );
    }
}

ReactDOM.render(<App/>, document.getElementById("container"));