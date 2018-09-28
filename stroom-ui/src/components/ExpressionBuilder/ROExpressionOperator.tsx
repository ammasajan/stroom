import * as React from "react";

import ROExpressionTerm from "./ROExpressionTerm";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

import { LineTo } from "../LineTo";
import {
  ExpressionOperatorType,
  ExpressionItem,
  ExpressionTermType
} from "../../types";

export interface Props {
  expressionId: string;
  operator: ExpressionOperatorType;
  isRoot?: boolean;
  isEnabled: boolean;
}

/**
 * Read only expression operator
 */
const ROExpressionOperator = ({
  expressionId,
  operator,
  isRoot,
  isEnabled
}: Props) => {
  let className = "expression-item expression-item--readonly";
  if (isRoot) {
    className += " expression-item__root";
  }
  if (!isEnabled) {
    className += " expression-item--disabled";
  }

  return (
    <div className={className}>
      <div>
        <span id={`expression-item${operator.uuid}`}>
          <FontAwesomeIcon icon="circle" />
        </span>
        <span>{operator.op}</span>
      </div>
      <div className="operator__children">
        {operator.children &&
          operator.children
            .map((c: ExpressionItem) => {
              let itemElement;
              const cIsEnabled = isEnabled && c.enabled;
              switch (c.type) {
                case "term":
                  itemElement = (
                    <div key={c.uuid} id={`expression-item${c.uuid}`}>
                      <ROExpressionTerm
                        expressionId={expressionId}
                        isEnabled={cIsEnabled}
                        term={c as ExpressionTermType}
                      />
                    </div>
                  );
                  break;
                case "operator":
                  itemElement = (
                    <ROExpressionOperator
                      expressionId={expressionId}
                      isEnabled={cIsEnabled}
                      operator={c as ExpressionOperatorType}
                    />
                  );
                  break;
                default:
                  throw new Error(`Invalid operator type: ${c.type}`);
              }

              // Wrap it with a line to
              return (
                <div key={c.uuid}>
                  <LineTo
                    lineId={c.uuid}
                    lineType="downRightElbow"
                    fromId={`expression-item${operator.uuid}`}
                    toId={`expression-item${c.uuid}`}
                  />
                  {itemElement}
                </div>
              );
            })
            .filter(c => !!c) // null filter
        }
      </div>
    </div>
  );
};

export default ROExpressionOperator;
