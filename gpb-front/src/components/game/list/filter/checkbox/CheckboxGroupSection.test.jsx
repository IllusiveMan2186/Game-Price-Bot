import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CheckboxGroupSection from './CheckboxGroupSection';

describe('CheckboxGroupSection', () => {
    const options = [
        { value: 'opt1', label: 'Option 1' },
        { value: 'opt2', label: 'Option 2' },
        { value: 'opt3', label: 'Option 3' },
    ];
    const title = 'Filter Title';
    const fieldName = 'genre';
    const fieldValue = ['opt2'];
    const isNotExcludedFieldType = true;

    let onChange;
    let isChecked;

    beforeEach(() => {
        onChange = jest.fn();
        isChecked = jest.fn((value, fieldValueArray, flag) =>
            flag && fieldValueArray.includes(value)
        );
    });

    it('should renders the section title', () => {
        render(
            <CheckboxGroupSection
                title={title}
                options={options}
                fieldName={fieldName}
                fieldValue={fieldValue}
                isNotExcludedFieldType={isNotExcludedFieldType}
                onChange={onChange}
                isChecked={isChecked}
            />
        );
        expect(screen.getByText(title)).toBeInTheDocument();
    });

    it('should renders a checkbox and label for each option', () => {
        render(
            <CheckboxGroupSection
                title={title}
                options={options}
                fieldName={fieldName}
                fieldValue={fieldValue}
                isNotExcludedFieldType={isNotExcludedFieldType}
                onChange={onChange}
                isChecked={isChecked}
            />
        );

        expect(isChecked).toHaveBeenCalledTimes(options.length);

        options.forEach(option => {
            const checkbox = screen.getByRole('checkbox', { name: option.label });
            expect(checkbox).toBeInTheDocument();
            expect(checkbox).toHaveAttribute('name', fieldName);
            expect(checkbox).toHaveAttribute('value', option.value);

            const expectedChecked = isChecked(option.value, fieldValue, isNotExcludedFieldType);
            expect(checkbox.checked).toBe(expectedChecked);
        });
    });

    it('should calls onChange with event and flag when checkbox is clicked', async () => {
        render(
            <CheckboxGroupSection
                title={title}
                options={options}
                fieldName={fieldName}
                fieldValue={fieldValue}
                isNotExcludedFieldType={isNotExcludedFieldType}
                onChange={onChange}
                isChecked={isChecked}
            />
        );

        const firstCheckbox = screen.getByRole('checkbox', { name: options[0].label });
        await userEvent.click(firstCheckbox);

        expect(onChange).toHaveBeenCalledTimes(1);
        const [eventArg, flagArg] = onChange.mock.calls[0];
        expect(eventArg.target).toBe(firstCheckbox);
        expect(flagArg).toBe(isNotExcludedFieldType);
    });
});