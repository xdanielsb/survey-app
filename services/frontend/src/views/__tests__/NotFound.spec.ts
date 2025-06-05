import { mount } from '@vue/test-utils'
import NotFound from '@/views/NotFound.vue'
import { describe, it, expect } from 'vitest'

describe('NotFound.vue', () => {
  it('renders 404 page content', () => {
    const wrapper = mount(NotFound)
    expect(wrapper.text()).toContain('404')
    expect(wrapper.text()).toContain('Page Not Found')
  })
})
