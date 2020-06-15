import { action } from "@storybook/addon-actions";
import { storiesOf } from "@storybook/react";
import * as React from "react";
import { Account } from "../types";
import {
  disabledUser,
  inactiveUser,
  lockedUser,
  newUser,
  wellUsedUser,
} from "testing/data/users";
import AccountForm from "./AccountForm";

interface Props {
  user: Account;
}

const onValidate = (
  password: string,
  verifyPassword: string,
  email: string,
) => {
  action("onValidate");
  return new Promise<string>(() => "");
};

const TestHarness: React.FunctionComponent<Props> = ({ user }) => {
  return (
    <AccountForm
      account={user}
      onBack={action("onBack")}
      onSubmit={action("onSubmit")}
      onCancel={action("onCancel")}
      onValidate={onValidate}
    />
  );
};

interface Test {
  [s: string]: Account;
}

const tests: Test = {
  "brand new": newUser,
  "well used": wellUsedUser,
  disabled: disabledUser,
  inactive: inactiveUser,
  locked: lockedUser,
};

const stories = storiesOf("Users/User Form", module);

Object.entries(tests)
  .map((k) => ({
    name: k[0],
    user: k[1],
  }))
  .forEach(({ name, user }) => {
    stories.add(name, () => <TestHarness user={user} />);
  });
